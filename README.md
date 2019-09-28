# N26-TRANSACTIONS-STATISTICS

<table>
  <tr>
    <th>Language</th>
    <td>Java 8</th>
  </tr>
  <tr>
    <th>Built Using</td>
    <td>SpringBoot 2.1.2,Junit 4 and Prometheus(for metrics monitoring)</td>
  </tr>
  <tr>
      <th>IDE used</td>
      <td>IntelliJ 2019.1</td>
    </tr>
  
</table>

## Code​ ​Challenge

We would like to have a restful API for our statistics. The main use case for our API is to calculate real-time
statistic from the last 60 seconds. There will be two APIs, one of them is called every time a transaction is made.
It is also the sole input of this rest API. The other one returns the statistic based of the transactions of the last
60 seconds.

## Specs
````
POST /transactions
````
Every time a new transaction happened, this endpoint will be called.

Body:
```
{
  "amount": "12.3343",
  "timestamp": "2018-07-17T09:59:51.312Z"
}
```

Where:
````

amount – transaction amount; a string of arbitrary length that is parsable as a BigDecimal
timestamp – transaction time in the ISO 8601 format YYYY-MM-DDThh:mm:ss.sssZ in the UTC timezone (this is not the current timestamp)
````
Returns: Empty body with either 201 or 204.
````
201 – in case of success
204 – if the transaction is older than 60 seconds
400 – if the JSON is invalid
422 – if any of the fields are not parsable or the transaction date is in the future
````

````
GET /statistics
````
This returns statistics of last 60 seconds transactions.
```
Response:
{
  "sum": "1000.00",
  "avg": "100.53",
  "max": "200000.49",
  "min": "50.23",
  "count": 10
}
```

```
Where:

sum – a BigDecimal specifying the total sum of transaction value in the last 60 seconds
avg – a BigDecimal specifying the average amount of transaction value in the last 60 seconds
max – a BigDecimal specifying single highest transaction value in the last 60 seconds
min – a BigDecimal specifying single lowest transaction value in the last 60 seconds
count – a long specifying the total number of transactions that happened in the last 60 seconds
All BigDecimal values always contain exactly two decimal places and use `HALF_ROUND_UP` rounding. eg: 10.345 is returned as 10.35, 10.8 is returned as 10.80


```


````
DELETE /transactions
````

```
This endpoint causes all existing transactions to be deleted

The endpoint should accept an empty request body and return a 204 status code.
```

#### Requirements

For the rest api, the biggest and maybe hardest requirement is to make the GET /statistics execute in constant time
and space. The best solution would be O(1). It is very recommended to tackle the O(1) requirement as the last thing
to do as it is not the only thing which will be rated in the code challenge. Other requirements, which are obvious,
but also listed here explicitly:

    ● The API have to be threadsafe with concurrent requests
    ● The API have to function properly, with proper result
    ● The project should be buildable, and tests should also complete successfully. e.g. If maven is used, then
       mvn clean install should complete successfully.
    ● The API should be able to deal with time discrepancy, which means, at any point of time, we could receive a
       transaction which have a timestamp of the past
    ● Make sure to send the case in memory solution without database (including in-memory database) ##Handling the solution wihtout database and having O(1) is biggest challenge.
    ● Endpoints have to execute in constant time and memory (O(1)).
## Prerequisites

The project requires Java 8 and Maven to build.

#### Build

    $ mvn clean install

#### Run

    $ mvn spring-boot:run

#### Test

    $ mvn clean integration-test

#### Included Actutators to do health check and for monitoring- attached screenshot of local metrics

    /actuator/health or  /actuator/metrics or  /actuator/info 
    /actuator/prometheus
    
#### Included Prometheus to do metrics monitoring for production level
##### query to run prometheus in 9090 port using docker

$ docker run -d --name=prometheus -p 9090:9090 -v <prometheus.yml>:/etc/

```