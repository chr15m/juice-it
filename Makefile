STATIC=$(shell find public/ -maxdepth 1 -type f | grep -v .swp)

build: $(shell find src) public/*
	mkdir -p build
	cp $(STATIC) build/
	touch build

node_modules: package.json
	npm i

.PHONY: watch clean

watch: node_modules
	npx shadow-cljs watch app 

repl: node_modules
	npx shadow-cljs cljs-repl app

clean:
	rm -rf build/*

