echo "hello world" >> test.txt
curl -X POST http://localhost:8181/api/data/hello -F "file=@test.txt"
