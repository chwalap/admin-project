#!/bin/sh

while true; do
sleep 10 && cat << EOF
{ "temperature": $(( $(shuf -i 0-40 -n 1 ) - 20 )) }
EOF
done
