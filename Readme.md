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
docker run --name some-mongo -d mongo
```

Récupérer l'adresse du conteneur docker et la remplacer dans les sources, ex: 
```
uri = "mongodb://172.17.0.2:27017"
```
 
Enfin, lancer la commande run

```
./gradlew run
```

Tester le service avec:
```
curl -X GET -i http://localhost:8080/companies
```

Sous licence MIT