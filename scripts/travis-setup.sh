#!/usr/bin/env bash

echo "Installing Ruby environment..."
rvm use 2.2.3 --install --fuzzy
gem update --system

echo "Installing Jekyll..."
gem install sass
gem install ruby_dep -v 1.3.1
gem install jekyll -v 3.2.1

echo "Installing JSDom..."
npm install jsdom