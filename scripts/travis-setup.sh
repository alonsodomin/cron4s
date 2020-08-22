#!/usr/bin/env bash

echo "Installing Ruby environment..."
rvm use 2.6.5 --install --fuzzy
gem update --system

echo "Installing Jekyll..."
gem install jekyll -v 4

echo "Installing Codecov..."
pip install --user codecov
