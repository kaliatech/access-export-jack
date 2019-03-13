docker build ./postgres -t kaliatech/postgres:1.0.0-SNAPSHOT
docker run -p 15432:5432 -d --name postgres-ajack kaliatech/postgres