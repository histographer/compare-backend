db.createUser({
    user: "user",
    pwd: "secret",
    roles: [ { role: "readWrite", db: "patornat" } ]
})

db.user.insert({
    name: "user"
})


