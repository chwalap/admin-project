import argparse
import json
import logging
import random
import time

import paho.mqtt.client as mqtt

parser = argparse.ArgumentParser()
parser.add_argument("hostname", help="MQTT broker host")
parser.add_argument("mqtt_topic", help="MQTT topic to publish the JSON payload")
parser.add_argument("min_value", type=float, help="Minimum value for the random range")
parser.add_argument("max_value", type=float, help="Maximum value for the random range")
args = parser.parse_args()

broker_host = args.hostname
mqtt_topic = args.mqtt_topic
min_value = args.min_value
max_value = args.max_value
broker_port = 1883

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(mqtt_topic)

logger.info("Waiting for Mosquitto...")

client = mqtt.Client()
client.connect(broker_host, broker_port)

logger.info("Mosquitto is up and running!")

while True:
    value = random.uniform(min_value, max_value)

    data = {
        mqtt_topic: value
    }
    json_payload = json.dumps(data)
    logger.info(json_payload)

    client.publish(mqtt_topic, json_payload)
    time.sleep(1)
