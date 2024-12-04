## Functionalities
A multithreaded web crawler in Java that starts from a given base_url and traverses all linked pages to search for a specific keyword


### Features

- [x] Web crawling
- [x] Url queue
- [x] Page content Cache
- [x] Decoupled code for crawler
- [ ] Improve keyword search?
- [ ] Add a limit for the crawling

### How to run
###### Build the docker file
```
docker build . -t yourimage/name
```
###### And now run the program passing a http base url as a variable
```
docker run -e BASE_URL=http://someurl.com -p port:port --rm yourimage/name
```

### Usage
###### To start a crawling process to the base_url:
```
POST /crawl HTTP/1.1
Host: localhost:port
Content-Type: application/json
Body: {"keyword": "foo"}
```

###### To get the crawling results:
```
GET /crawl/id_received_by_post HTTP/1.1
Host: localhost:port
```
