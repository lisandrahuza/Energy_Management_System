import pika

def callback(ch, method, properties, body):
    print(f"Mesaj primit: {body}")

def consuma_mesaje():
    connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
    channel = connection.channel()
    channel.queue_declare(queue='measurements')

    channel.basic_consume(queue='measurements', on_message_callback=callback, auto_ack=True)

    print('Aștept mesaje. Apasă CTRL+C pentru a opri.')
    channel.start_consuming()

consuma_mesaje()
