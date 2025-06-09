
set PWD=%cd%

docker build -t cpp_builder .

docker run --rm -ti --mount type=bind,src=%PWD%,dst=/cpp cpp_builder 
