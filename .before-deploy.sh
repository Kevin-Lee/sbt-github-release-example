#!/bin/bash

export PROJECT_VERSION=`cat target/version.sbt`

echo "PROJECT_VERSION=$PROJECT_VERSION"

if [ ! -n "$PROJECT_VERSION" ]
  then
  echo "NO PROJECT_VERSION is found so quit!"
  exit 1
fi

if [ -n "$GITHUB_OAUTH" ]
  then
  echo "Writing GitHub OAuth to $HOME/.github"
  echo "login = Kevin-App-Builder" > $HOME/.github
  echo "oauth = $GITHUB_OAUTH" >> $HOME/.github

#  echo "Writing GitHub OAuth to $HOME/.netrc"
#  echo "machine github.com" > $HOME/.netrc
#  echo "  login $GITHUB_OAUTH" >> $HOME/.netrc
fi

# export GIT_TAG="Release-v$TRAVIS_BUILD_NUMBER"
export GIT_TAG="Release-v$PROJECT_VERSION"
echo "GIT_TAG=$GIT_TAG"
export PROJECT_BUILD_NAME="$GIT_TAG"
echo "PROJECT_BUILD_NAME=$PROJECT_BUILD_NAME"

echo "check git ls-remote --exit-code --tags origin $GIT_TAG 2>&1 > /dev/null"

if git ls-remote --exit-code --tags origin $GIT_TAG 2>&1 > /dev/null ; then
  echo "the given tag '$GIT_TAG' already exists so skip it!"
else
  echo "the given tag '$GIT_TAG' does not exist so run it!"
  git config --global user.email "builder+github@lckymn.com"
  git config --global user.name "Kevin-App-Builder"

  git tag "$GIT_TAG" -a -m "Automatically generated tag by Travis CI for build $TRAVIS_BUILD_NUMBER"
  git push git@github.com:Kevin-Lee/sbt-github-release-example --tags

  echo "======================================================"
  echo "ls -l target/scala-2.11/*-one-jar.jar"
  ls -l target/scala-2.11/*-one-jar.jar
  echo "======================================================"
  if [ -d "target/bin-all" ]; then
    echo "Clean up existing target/bin-all/*"
    echo "rm -R target/bin-all/*"
    rm -R target/bin-all/*
    echo "------------------------------------------------------"
  fi
  echo "Create a folder to put all the binary files."
  echo "------------------------------------------------------"
  echo "mkdir -p target/bin-all/$PROJECT_BUILD_NAME"
  mkdir -p "target/bin-all/$PROJECT_BUILD_NAME"
  echo "ls -l target/bin-all/$PROJECT_BUILD_NAME"
  ls -l "target/bin-all/$PROJECT_BUILD_NAME"

  echo "------------------------------------------------------"
  echo "cp target/scala-2.11/*-one-jar.jar target/bin-all/$PROJECT_BUILD_NAME/"
  cp target/scala-2.11/*-one-jar.jar "target/bin-all/$PROJECT_BUILD_NAME/"
  echo "------------------------------------------------------"
  echo "ls -lR target/bin-all/$PROJECT_BUILD_NAME/"
  ls -lR "target/bin-all/$PROJECT_BUILD_NAME"
  echo "------------------------------------------------------"
  echo "Copying all binary files to 'target/bin-all', Done!"
  echo "======================================================"
fi
