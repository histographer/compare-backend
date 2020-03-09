echo "db.createUser({ user:  '$COMPARE_MONGODB_USERNAME', pwd: '$COMPARE_MONGODB_PASSWORD', roles: [{ role: 'userAdminAnyDatabase', db: 'admin'}] })" | mongo admin



