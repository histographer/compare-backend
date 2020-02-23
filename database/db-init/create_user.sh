echo "db.createUser({ user:  '$PATORNAT_MONGODB_USERNAME', pwd: '$PATORNAT_MONGODB_PASSWORD', roles: [{ role: 'userAdminAnyDatabase', db: 'admin'}] })" | mongo admin



