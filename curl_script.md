
curl -X GET -i http://localhost:8080/company/SG

curl -X GET -i http://localhost:8080/companies

curl -X GET -i http://localhost:8080/consultant/Pierre

curl -X PUT -d '{"name":"Pierre X","forename":"Dupont Y"}' -i http://localhost:8080/consultant/Pierre