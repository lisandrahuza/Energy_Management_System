# Folosim o imagine de bază cu Python
FROM python:3.9-slim

# Setăm directorul de lucru în container
WORKDIR /app

# Copiem fișierele necesare în container
COPY . /app

# Instalăm dependențele necesare
RUN pip install --no-cache-dir -r requirements.txt

# Setăm variabilele de mediu pentru RabbitMQ
ENV RABBITMQ_HOST=rabbitmq
ENV RABBITMQ_USER=guest
ENV RABBITMQ_PASSWORD=guest

# Rulăm scriptul Python
CMD ["python", "citire.py"]
