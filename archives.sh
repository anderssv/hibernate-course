rm *.zip
git checkout master
zip -r solution.zip * -x "target/*" README archives.sh
git checkout start_exercise
zip -r assignment.zip * -x "target/*" README
