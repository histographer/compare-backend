TODO update

### Post - /scoring
```json 
{
  "projectId": 9999,
  "chosen": {
    "id": 1,
    "comment": "testcomment"
  },
  "other": {
    "id": 2,
    "comment": "testcomment2"
  }
}
```

### Post - /session
#### Request
```json
{ 
    "projectId": 35549999,
    "monitorType": "IPS laptop",
    "hospital": "St. Olavs"
}
```
### Get - /session
Invalidates / logs out a session. Has to have query parameter `/session?logout=true`

### Post - /project
This will add a project and insert images into the database. The active status defaults to false
#### Request
```json
{
  "projectId": 994994
}
```

### POST - /project/update
This will update a project and return the new values. 

#### Request
```json
{
  "projectId": 12345,
  "active": true
}

```

#### Response
```json
{
    "name": "IT2901 eksempelprosjekt",
    "id": 58003,
    "active": true
}
```


### Get - /project
This will get all projects available if no query string is attached
#### Response
```json
[
    {
        "name": "IT2901 Rutinefarge  2019-Q4 ranking",
        "id": 983488,
        "active": false
    },
    {
        "name": "IT2901 eksempelprosjekt",
        "id": 8485899,
        "active": true
    }
]
```
#### Get single project with query string
To get a single project use query string `/project?projectId=99349`
#### Response
```json
{
    "name": "IT2901 eksempelprosjekt",
    "id": 58003,
    "active": true
}
```

### Get - /imagePair
Gets a new pair for comparison with querystring `/imagePair?projectId=99999` 

#### Response
```json
[
    {
        "imageServerURLs": [
            "url2",
            "url2"
        ],
        "depth": 9,
        "magnification": 40,
        "mime": "openslide/ndpi",
        "width": 78848,
        "id": 385624,
        "projectId": 99999,
        "resolution": 0.22059471905231476,
        "height": 37632
    },
    {
        "imageServerURLs": [
            "url1"
        ],
        "depth": 9,
        "magnification": 40,
        "mime": "openslide/ndpi",
        "width": 89600,
        "id": 385831,
        "projectId": 99999,
        "resolution": 0.22059471905231476,
        "height": 59136
    }
]
```

### Get - /ranking 
Returns an array of rankings. Needs a query string `/ranking?projectId=99999`

#### Returns
```json
[
    {
        "score": 1000,
        "rankings": 0,
        "id": 4011,
        "fileName": "an_image.png"
    },
    {
        "score": 1000,
        "rankings": 0,
        "id": 78860,
        "fileName": "another_image.jpeg"
    }
]
```