#!/bin/sh

rm -rf data/kafka/data/*
rm -rf data/mongo/data/*
rm -rf data/mosquitto/data/*
rm -rf data/mosquitto/logs/*
rm -rf data/zookeeper/data/*
rm -rf data/zookeeper/logs/*

echo "All data removed!"
