call docker run -it --rm --name mqtt-publisher --network admin-project_default efrecon/mqtt-client ^
pub -h mosquitto  -t asdf -m "{\"id\":1,\"message\":\"dupa\"}"
