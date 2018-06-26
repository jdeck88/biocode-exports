#!/bin/bash
/* Script to generate INDO configuration file and push to Geome Server
  header contains header elements, including lists
  samples.json == sample entity
  events.json == event entity
  tissues.json == tissue entity
  footer.json == wrapping up
*/

rm -f indo.json
rm -f tmp.json

echo "Generating JSON file..."
cat header.json > tmp.json 
cat samples.json >> tmp.json 
echo "},{" >> tmp.json
cat events.json >> tmp.json 
echo "},{" >> tmp.json 
cat tissues.json  >> tmp.json
echo "}" >> tmp.json
cat footer.json >> tmp.json


echo "Pretty printing file ..."
python -m json.tool tmp.json > indo.json
retVal=$?
if [ $retVal -eq 0 ]; then
    echo "Success!"
else
    echo "Failed, exiting";
    exit $retVal
fi

echo "Running JSONLint on file ..."
jsonlint indo.json -q
retVal=$?
if [ $retVal -eq 0 ]; then
    echo "Success!"
else
    echo "Failed, exiting";
    exit $retVal
fi

echo "Putting JSON to server...."
# put this which validates
#curl -X PUT -H 'Content-Type: application/json' --data "@/Users/jdeck/Downloads/indo/indo.json" https://api.develop.geome-db.org/projects/34/config?access_token=yMpc5txpx-p-zn3zhy2M
