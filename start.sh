#!/bin/bash

ATTACH_CMD="docker logs --follow"
COURIER_API="courier_courier_api_1"
COURIER_UI="courier_courier_ui_1"
AUTHENTITY_API="courier_authentity_api_1"
AUTHENTITY_UI="courier_authentity_ui_1"

docker-compose up --build --remove-orphans --force-recreate --detach
gnome-terminal --tab --command="${ATTACH_CMD} ${COURIER_API}" \
               --tab --command="${ATTACH_CMD} ${AUTHENTITY_API}" \
               --tab --command="${ATTACH_CMD} ${COURIER_UI}" \
               --tab --command="${ATTACH_CMD} ${AUTHENTITY_UI}"

read -n 1 -p "Press any key to stop the containers."

echo "Stopping containers..."
docker-compose down --remove-orphans
