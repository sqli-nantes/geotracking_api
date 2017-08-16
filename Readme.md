API micro services permettant la géolocalisation des missions des collaborateurs. La géolocalisation n'est pas dynamique, elle est spécifiée une fois en bdd pour chaque collaborateur, il ne s'agit pas de suivre en temps réel les collaborateurs, mais de connaitre leurs lieux de mission.

# Contexte de développement
## Techno
Ce projet s'appuie sur les technologies suivantes:
* Kotlin
* Vert.x
* Gradle
* MongoDB
* Docker

## Environnement de dév'
### Prérequis
Avoir installé Java 8, Gradle et Docker

### Installation
```
git clone
```

On lance le conteneur docker en background
```
docker run --name mongo-host -d mongo
```

Setter les variables d'environnement (sous linux)
```
    export MONGO_URI=mongodb://mongo-host:27017
    export MONGO_DB=geotracking-db
```
 
Enfin, lancer la commande run

```
./gradlew run 
```

Tester le service avec:
```
curl -X GET -i http://localhost:8080/companies
```

### Utilisation Docker
* build
```
    docker build -t geotracking-api:1 .
```

* run depuis le répertoire des sources (ou modifier `$(pwd)` sinon)
```
    docker run -d --name geotracking-api --link mongo-host:mongo-host geotracking-api:1
```

### Spécifications de l'API

#### Pour utiliser l'API sous swagger

* Image swagger-ui pré buildée

```
docker pull swaggerapi/swagger-ui
```
* On lance le conteneur 
```
sudo docker run -p 80:8080 -e "SWAGGER_JSON=/swagger/swagger.yaml" -v $(pwd):/swagger swaggerapi/swagger-ui
```

# Licence
Sous licence MIT