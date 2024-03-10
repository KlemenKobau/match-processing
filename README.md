# Match processing

## Project requirements

This project requires the following to be installed.
- maven and java 17
- docker and docker-compose

## Results

The performance was tested on my machine with the following specification:
```
Docker version 24.0.5, build ced0996
openjdk 17.0.10 2024-01-16
Apache Maven 3.6.3

Intel(R) Core(TM) i7-3770 CPU @ 3.40GHz
16GB ram
```

The processing time was around 40s.
You can calculate your running time by running the
following query on the database.

```postgres-psql
select count(id),min(createdat), max(createdat) from "public"."match";
```

The result on my machine was:

| Count  | min                      | max                      |
|--------|--------------------------|--------------------------|
| 302536 | Mar 10, 2024, 7:05:08 PM | Mar 10, 2024, 7:05:47 PM |



## Building project components

To build the `match consumer` run:
```bash
cd match-consumer
./mvnw install -Dquarkus.container-image.build=true
cd ..
```

To build the `match publisher` run:
```bash
cd match-publisher
./mvnw install -Dquarkus.container-image.build=true
cd ..
```

## Processing example data

To start processing run the following command in the
root project directory:
```bash
docker-compose up -d
```

Wait around two minutes to make sure that the process is finished.

## Test
Build the `match validator` by running:
```bash
cd match-validator
./mvnw install -Dquarkus.container-image.build=true
cd ..
```

To test the result in the database, run:
```bash
docker-compose up match-validator
```

## Cleanup

To remove **all** data from the previous test, run the following command.
This will also remove the volumes that the database uses to save data.
```bash
docker-compose down --volumes
```