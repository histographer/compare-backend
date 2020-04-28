# API documentation

## POST /scoring
Creates an image comparison. The user must have an active session.

### Example request body

```json 
{
  "projectId": 9999,
  "chosen": {
    "id": 1,
    "comment": "An optional comment about the image"
  },
  "other": {
    "id": 2,
  }
}

```

## POST /session
Creates a session for the user.

### Example request body

```json
{ 
  "projectId": 35549999,
  "monitorType": "IPS laptop",
  "hospital": "St. Olavs"
}

```
## GET /session?logout=true
Invalidates / logs out a session.

## POST /project
Adds a Cytomine project to the set of available projects. Data about the project's images will be inserted into the database. The project will initially be marked as inactive.

### Example request body

```json
{
  "projectId": 994994
}

```

## POST /project/update
Updates a project's active status.

### Example request body
```json
{
  "projectId": 12345,
  "active": true
}

```

### Example response
```json
{
  "name": "IT2901 eksempelprosjekt",
  "id": 58003,
  "active": true
}

```


## GET /project
Gets all available projects.

### Example response
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

## GET /project?projectId=[id]
Gets a single project.

### Example response
```json
{
  "name": "IT2901 eksempelprosjekt",
  "id": 58003,
  "active": true
}

```

## GET /imagePair?projectId=[id]
Gets a new pair for comparison. The user must have an active session.

The optional query parameter `skipped` can be used to prevent certain image pairs from being returned. The parameter must be a JSON array of pairs of image IDs, e.g. `[[1, 2], [1, 4]]`. Note that the array must be URL coded.

### Example response
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

## GET /ranking?projectId=[id]
Gets a ranking of the images in a project based on the comparisons made by users.

### Example response
```json
[
  {
    "score": 1000,
    "rankings": 3,
    "id": 4011,
    "fileName": "an_image.png"
  },
  {
    "score": 1000,
    "rankings": 5,
    "id": 78860,
    "fileName": "another_image.jpeg"
  }
]

```
