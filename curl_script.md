
curl -X GET -i http://localhost:8080/company/SG

curl -X GET -i http://localhost:8080/companies

curl -X GET -i http://localhost:8080/people

curl -X GET -i http://localhost:8080/people/name/Pierre

curl -X GET -i http://localhost:8080/people/forename/Dupont

curl -X PUT -d '{"name":"Pierre X","forename":"Dupont Y"}' -i http://localhost:8080/person/id/XXXXXXXXXXXXXXXX

curl -X PUT -d '{"name":"Pierre X"}' -i http://localhost:8080/consultant/Pierre & curl -X GET -i http://localhost:8080/companies