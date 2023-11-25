# ASE Project Backend

For technical help check [WIKI](https://gitlab.lrz.de/ase-22-23/team13/ase-project-backend/-/wikis/home)

The latest version is V2.

# Getting Started With Local Deployment
Follow below steps:
## Clone Repo
```bash
git clone git@gitlab.lrz.de:ase-22-23/team13/ase-project-backend.git
```

## Install
```bash
mvn clean install
```

## Docker compose build under dev environment
```bash
docker-compose -f ./docker-compose-dev.yml build --no-cache
```

## Docker compose up under dev environment
```bash
docker-compose -f ./docker-compose-dev.yml up
```

