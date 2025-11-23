export BLV_PROJ_ROOT=${PWD}
function ESMAGAR() {
	sudo docker container stop bluevelvet_db
	sudo docker container rm bluevelvet_db
 }

function iniciar() {
	cd ${BLV_PROJ_ROOT}
	sudo docker-compose up -d

}
