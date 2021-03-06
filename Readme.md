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

Il existe deux options:

1- Swagger ui en ligne
* Se rendre sur cette adresse: http://petstore.swagger.io/
* Insérer l'URL de la description YAML, ou cliquer sur le lien: http://petstore.swagger.io/?url=https://raw.githubusercontent.com/sqli-nantes/geotracking_api/master/swagger.yaml
* On peut naviguer dans notre description d'API et la tester si CORS a été activé

2- Conteneur docker en local
* On charge l'image swagger-ui pré buildée

```
docker pull swaggerapi/swagger-ui
```
* On lance le conteneur 
```
docker run -p 80:8080 -e "SWAGGER_JSON=/swagger/swagger.yaml" -v $(pwd):/swagger swaggerapi/swagger-ui
```

### TODO
* Spécifier les chemins au pluriel ex: Get: /people/{id}
* La recherche par rapport au nom doit être en paramètre de recherche, ex: Get /people?name=Jean
* Supprimer l'enveloppe "result" de la réponse, ex: 
    - Get /people => []
    - Get /people/{id} => {name:...}
* Ajouter le nom d'API dans l'URI, ex: /colabloc/people/

# Licence
Sous licence MIT