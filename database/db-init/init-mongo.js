db.createUser({
    user: "user",
    pwd: "secret",
    roles: [ { role: "readWrite", db: "users" } ]
})

db.users.insert({
    name: "user"
})

