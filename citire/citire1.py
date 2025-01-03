import threading
import uuid
import pika
import csv
import time
import json
import logging
from datetime import datetime

# Configurați logarea
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

# UUID pentru cele două dispozitive
DEFAULT_DEVICE_ID_1 = uuid.UUID("e82aea05-127c-4228-a759-b6c2713a359e")
DEFAULT_DEVICE_ID_2 = uuid.UUID("b09ce933-18cd-48f5-8cb0-1fe99a9e255e")

# Configurație RabbitMQ
RABBITMQ_HOST = 'host.docker.internal'  # pentru a accesa RabbitMQ de pe gazdă
RABBITMQ_USER = 'guest'
RABBITMQ_PASSWORD = 'guest'

# Funcție pentru citirea datelor din fișier CSV
def citeste_csv(file_path):
    with open(file_path, mode='r') as file:
        csv_reader = csv.DictReader(file)
        data = []
        for row in csv_reader:
            if '0' in row:
                row['measurements_value'] = row.pop('0')
            try:
                row['measurements_value'] = float(row['measurements_value'])
            except ValueError:
                logging.warning(f"Valoare invalidă pentru measurements_value: {row['measurements_value']}, se va seta la 0.0")
                row['measurements_value'] = 0.0
            data.append(row)
    return data

# Funcție pentru trimiterea mesajelor la coadă
def trimite_la_coada(channel, mesaj, queue_name):
    try:
        channel.basic_publish(
            exchange='',
            routing_key=queue_name,
            body=mesaj,
            properties=pika.BasicProperties(
                delivery_mode=2  # Mesaj persistent
            )
        )
        logging.info(f"Trimis în coada '{queue_name}': {mesaj}")
    except Exception as e:
        logging.error(f"Eroare la trimiterea mesajului: {e}")

# Funcție pentru trimiterea datelor de la un dispozitiv
def trimite_date(file_path, device_id, queue_name):
    credentials = pika.PlainCredentials(RABBITMQ_USER, RABBITMQ_PASSWORD)
    try:
        # Stabilirea conexiunii la RabbitMQ
        connection = pika.BlockingConnection(pika.ConnectionParameters(
            host=RABBITMQ_HOST,  # Folosește 'host.docker.internal' pentru a accesa RabbitMQ de pe gazdă
            port=5672,
            credentials=credentials
        ))
        logging.info("Conexiunea la RabbitMQ a fost realizată cu succes!")
        channel = connection.channel()

        # Declararea cozii ca fiind durabilă (dacă nu există deja)
        try:
            channel.queue_declare(queue=queue_name, durable=True, passive=True)
        except pika.exceptions.ChannelClosedByBroker as e:
            logging.error(f"Coada '{queue_name}' există deja cu setările de durabilitate diferite: {e}")
            return  # Oprește execuția dacă coada există deja cu setări incompatibile

        date_csv = citeste_csv(file_path)

        try:
            for date in date_csv:
                timestamp = int(datetime.now().timestamp() * 1000)
                date['timestamp'] = timestamp
                date['id_device'] = str(device_id)
                mesaj = json.dumps(date)
                trimite_la_coada(channel, mesaj, queue_name)
                time.sleep(5)  # O pauză între mesaje pentru a nu supraîncărca coada
        finally:
            connection.close()
            logging.info(f"Conexiunea la RabbitMQ pentru coada '{queue_name}' a fost închisă.")
    except pika.exceptions.AMQPConnectionError as e:
        logging.error(f"Conexiunea la RabbitMQ a eșuat: {e}")
    except Exception as e:
        logging.error(f"A apărut o eroare neașteptată: {e}")

# Funcția principală care creează firele de execuție
def main():
    file_path = 'D:/an4_sem1/SD/Tema2_Proiect2HuzaLisandra/citire/sensor.csv'  # Calea către fișierul CSV

    # Crearea thread-urilor pentru cele două dispozitive
    thread1 = threading.Thread(target=trimite_date, args=(file_path, DEFAULT_DEVICE_ID_1, 'measurements'))
    thread2 = threading.Thread(target=trimite_date, args=(file_path, DEFAULT_DEVICE_ID_2, 'measurements1'))

    # Pornirea thread-urilor
    thread1.start()
    thread2.start()

    # Așteaptă finalizarea thread-urilor
    thread1.join()
    thread2.join()

if __name__ == "__main__":
    main()
