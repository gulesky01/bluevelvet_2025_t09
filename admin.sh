export BLV_PROJ_ROOT=${PWD}
function ESMAGAR() {
	cd ${BLV_PROJ_ROOT}
	sudo docker compose down -v
}

function iniciar() {
	cd ${BLV_PROJ_ROOT}
	sudo docker-compose up -d --force-recreate

}
