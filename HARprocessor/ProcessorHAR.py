import numpy as np
from tensorflow import keras
import socket
import psycopg2
import os
import pika


def config_web_socket():
    soc = socket.socket()
    if soc == -1:
        raise Exception('Unsuccessful completion, socket() shall return a non-negative integer, returned -1 instead!')
    else:
        host = "localhost"
        port = 2004
        soc.bind((host, port))
        soc.listen(5)
        return soc


def process_input_list(input_list_from_socket):
    # from string to list of floats
    input_list_from_socket = input_list_from_socket[1:len(input_list_from_socket) - 1]
    input_list_from_socket = input_list_from_socket.split(', ')
    input_list_from_socket = list(map(float, input_list_from_socket))

    if len(input_list_from_socket) != 900:
        print('Input size must be 900, but received ', len(input_list_from_socket), "instead! ")
        return []
    else:
        accX = input_list_from_socket[:100]
        accY = input_list_from_socket[100:200]
        accZ = input_list_from_socket[200:300]
        gyroX = input_list_from_socket[300:400]
        gyroY = input_list_from_socket[400:500]
        gyroZ = input_list_from_socket[500:600]
        linAccX = input_list_from_socket[600:700]
        linAccY = input_list_from_socket[700:800]
        linAccZ = input_list_from_socket[800:900]
        res_list = list(zip(accX, accY, accZ,
                            gyroX, gyroY, gyroZ,
                            linAccX, linAccY, linAccZ))
        r_array = np.asarray(res_list)
        r_3d = np.expand_dims(r_array, axis=0)
        return r_3d

def process_input_list_forAccOnly(input_list_from_socket):
    # from string to list of floats
    input_list_from_socket = input_list_from_socket[1:len(input_list_from_socket) - 1]
    input_list_from_socket = input_list_from_socket.split(', ')
    input_list_from_socket = list(map(float, input_list_from_socket))

    if len(input_list_from_socket) != 300:
        print('Input size must be 300, but received ', len(input_list_from_socket), "instead! ")
        return []
    else:
        accX = input_list_from_socket[:100]
        accY = input_list_from_socket[100:200]
        accZ = input_list_from_socket[200:300]
        res_list = list(zip(accX, accY, accZ))
        r_array = np.asarray(res_list)
        r_3d = np.expand_dims(r_array, axis=0)
        return r_3d

def config_database():
    db_name = "PhoneSensorData"
    db_user = "postgres"
    db_pass = "rares"
    db_host = "localhost"
    db_port = "5432"

    conn_db = psycopg2.connect(database=db_name, user=db_user, password=db_pass, host=db_host, port=db_port)

    return conn_db


def config_queue_chanel():
    url = os.environ.get('CLOUDAMQP_URL', 'amqps://qnovhajz:3Mqw-uz3laEhM3mGfI9a-XHPNztW76ln@goose.rmq2.cloudamqp.com'
                                          '/qnovhajz')
    params = pika.URLParameters(url)
    connection = pika.BlockingConnection(params)
    channel = connection.channel()
    channel.queue_declare(queue='dataPhoneQueue2')

    return channel


def insert_prediction_into_database(duration_in_seconds, label, pulse_variation):
    connection_database = config_database()
    cursor_for_database = connection_database.cursor()
    insert_query = """INSERT INTO public.feedback_data (duration_in_seconds, label, pulse_variation) VALUES (%s,%s,%s)"""
    record_to_insert = (duration_in_seconds, label, pulse_variation)
    cursor_for_database.execute(insert_query, record_to_insert)
    connection_database.commit()


def most_frequent(list_of_predictions):
    no_appearances = {}
    count, itm = 0, ''
    for item in reversed(list_of_predictions):
        no_appearances[item] = no_appearances.get(item, 0) + 1
        if no_appearances[item] >= count:
            count, itm = no_appearances[item], item
    return itm


########################################################################################################################

model = keras.models.load_model(r'C:\Users\User\PycharmProjects\pythonProject\licentaHAR\model.h5')
labels = ['SmokeSD', 'SmokeST', 'Eat', 'DrinkSD', 'DrinkST', 'Sit', 'Stand']
labels.sort()

# establish connection with the server, through socket
server_socket = config_web_socket()

# establish connection with the queue instance, from CloudAMQP
queue_channel = config_queue_chanel()

# array to store the predictions that ware made
last_predictions = []

# as long the connection is up do:
while True:
    conn, addr = server_socket.accept()
    msg_from_server = conn.recv(int.from_bytes(conn.recv(2), byteorder='big')).decode("UTF-8")

    # create array of 3 dimensions to match model input type
    inputArray3d = process_input_list_forAccOnly(msg_from_server)

    # use model to predict the input received
    predictions = model.predict(np.asarray(inputArray3d))
    prediction_result = labels[np.argmax(predictions)]
    print(prediction_result)
    queue_channel.basic_publish(exchange='', routing_key='pdfprocess', body=prediction_result)

    last_predictions.append(prediction_result)

    # when we get 10 prediction, we find the most frequent one, and we store it in database
    if len(last_predictions) == 10:
        most_frequent_activity = most_frequent(last_predictions)
        # insert final prediction into server's database
        duration = 100  # mock values for future implementations
        pulse = "moderate"  # mock values for future implementations
        insert_prediction_into_database(duration, prediction_result, pulse)
