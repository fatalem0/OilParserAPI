# Project's goal
OilParserAPI provides an API to get the price information and statistic of a csv file

## Technologies
- akka-http
- pureconfig
- circe
- kantan.csv
- docker

# Deployment
Build the container image:  ```docker build -t oil-parser-api .``` (it takes a long time to build the container)

Run already existing container: ```docker run -it --rm %name_of_your_container%```
