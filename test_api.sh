
# 1. Register
echo "Registering..."
curl -X POST "http://localhost:8081/api/student/register" \
  -d "username=testuser" \
  -d "password=password" \
  -d "name=TestUser" \
  -d "email=test@example.com"
echo ""

# 2. Login and save cookie
echo "Logging in..."
curl -c cookie.txt -X POST "http://localhost:8081/api/student/login" \
  -d "username=testuser" \
  -d "password=password"
echo ""

# 3. List Projects
echo "Listing Projects..."
curl "http://localhost:8081/api/project/list?pageIndex=1&pageSize=5" > projects.json
echo ""

# 4. Submit Application (using hardcoded projectId 1001 from data.sql)
echo "Submitting Application..."
curl -b cookie.txt -X POST "http://localhost:8081/api/application/submit" \
  -H "Content-Type: application/json" \
  -d "{\"projectId\": \"1001\", \"documents\": [\"/upload/test.pdf\"]}"
echo ""
