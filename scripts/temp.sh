#!/bin/sh

while true; do
sleep 2 && cat << EOF
{ "temperature": $(( $(shuf -i 0-40 -n 1 ) - 10 )) }
EOF
done
