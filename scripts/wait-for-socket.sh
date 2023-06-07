#!/bin/sh

while ! nc -z $1 $2 > /dev/null 2>&1
do
  sleep 3
done
