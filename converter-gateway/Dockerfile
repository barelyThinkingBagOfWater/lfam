FROM python:3-slim
RUN apt-get update && apt-get -y install gcc
RUN mkdir /app
WORKDIR /app
ADD . /app/
RUN pip install -r app/requirements.txt

CMD ["python", "app/converter-gateway.py"]
