#!/bin/sh

while true; do
sleep 2 && cat << EOF
{ "time": $( date +%s ), "temperature": $(( $(shuf -i 0-40 -n 1 ) - 20 )) }
EOF
done
