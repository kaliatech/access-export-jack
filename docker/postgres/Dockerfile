FROM postgres:10.7-alpine
COPY db-password.txt /run/secrets/db-password
ENV POSTGRES_PASSWORD_FILE=/run/secrets/db-password