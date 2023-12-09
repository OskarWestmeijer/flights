#!/bin/bash

echo 'Start deploy maps script.'

compose_down="docker compose -f cprod.yml down"
list_images="docker images"
rm_latest_api="docker image rm oskarwestmeijer/maps-api:latest"
rm_latest_ui="docker image rm oskarwestmeijer/maps-ui:latest"
compose_up="docker compose -f cprod.yml up -d"
proxy_restart="docker restart reverse-proxy"

$compose_down
$list_images
$rm_latest_api
$rm_latest_ui
$compose_up
$proxy_restart

echo 'Finish deploy maps script.'
