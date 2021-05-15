#!/bin/bash

TEMP=$(( $( shuf -i 0-40 -n 1 ) - 20 ))
cat << EOF
{
  "temperature": ${TEMP}
}
EOF
