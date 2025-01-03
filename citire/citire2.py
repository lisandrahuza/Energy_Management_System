import uuid

import pika
import csv
import time
import json  # Pentru formatarea mesajului în JSON
from datetime import datetime

DEFAULT_DEVICE_ID_1= uuid.UUID("efbbbf30-7836-3739-4644-464639414639")
def citeste_csv(file_path):
    with open(file_path, mode='r') as file:
        csv_reader = csv.DictReader(file)
        data = []
        for row in csv_reader:
            # Redenumește cheia '0' în 'measurements_value'
            if '0' in row:
                row['measurements_value'] = row.pop('0')

            # Convertește valoarea 'measurements_value' într-un float
            try:
                row['measurements_value'] = float(row['measurements_value'])
            except ValueError:
                # Dacă nu se poate face conversia într-un float, setează o valoare implicită (de ex., 0.0)
                print(f"Valoare invalidă pentru measurements_value: {row['measurements_value']}, se va seta la 0.0")
                row['measurements_value'] = 0.0

            data.append(row)
    return data


def trimite_la_coada(channel, mesaj):
    """Trimite mesajul la coada RabbitMQ."""
    channel.basic_publish(
        exchange='',
        routing_key='measurements1',
        body=mesaj
    )
    print(f"Trimis: {mesaj}")


def main():
    # Configurare conexiune RabbitMQ
    connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
    channel = connection.channel()

    # Creare coadă
    channel.queue_declare(queue='measurements1')

    # Citire date din fișier CSV
    file_path = 'D:/an4_sem1/SD/citire/sensor.csv'
    date_csv = citeste_csv(file_path)

    try:
        # Trimiterea datelor la fiecare 5 secunde
        for date in date_csv:
            # Adaugă timestamp-ul curent în milisecunde
            timestamp = int(datetime.now().timestamp() * 1000)

            date['timestamp'] = timestamp
            # Convertim UUID în string înainte de a-l adăuga
            date['id_device'] = str(DEFAULT_DEVICE_ID_1)

            # Transformă datele în JSON pentru trimitere
            mesaj = json.dumps(date)

            # Trimite mesajul la RabbitMQ
            trimite_la_coada(channel, mesaj)

            # Așteaptă 2 secunde
            time.sleep(5)
    finally:
        # Închide conexiunea după ce toate datele au fost trimise
        connection.close()


if __name__ == "__main__":
    main()

