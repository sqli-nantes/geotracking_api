
curl -X GET -i http://localhost:8080/company/SG

curl -X GET -i http://localhost:8080/companies

curl -X GET -i http://localhost:8080/people

curl -X GET -i http://localhost:8080/person/name/Pierre

curl -X GET -i http://localhost:8080/person/forename/Dupont

curl -X PUT -d '{"name":"Pierre X","forename":"Dupont Y"}' -i http://localhost:8080/person/Pierre

curl -X PUT -d '{"name":"Pierre X"}' -i http://localhost:8080/consultant/Pierre & curl -X GET -i http://localhost:8080/companies