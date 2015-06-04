#!/bin/bash

cleanUp() {
  if [ -e "$HOME/.github" ]
    then
    echo "Remove $HOME/.github"
    rm $HOME/.github
  fi
  if [ -e "$HOME/.s3credentials" ]
    then
    echo "Remove $HOME/.s3credentials"
    rm $HOME/.s3credentials
  fi
}

sbt clean
sbt writeVersion
sbt test
sbt one-jar
export THIS_BRANCH="$CI_BRANCH"
if [ "$THIS_BRANCH" == "release" ];
  then

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

  if [ -n "$S3_INFO" ]
    then
    echo "Writing S3 INFO to $HOME/.s3credentials"
    echo -e "$S3_INFO" > $HOME/.s3credentials
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

    git tag "$GIT_TAG" -a -m "Automatically generated tag by Travis CI for $GIT_TAG"
    git push git@github.com:Kevin-Lee/sbt-github-release-example --tags

    echo "======================================================"
    echo "ls -l target/scala-2.11/*-one-jar.jar"
    ls -l target/scala-2.11/*-one-jar.jar
    echo "======================================================"
    if [ -d "target/ci" ]; then
      echo "Clean up existing target/ci/*"
      echo "rm -R target/ci/*"
      rm -R target/ci/*
      echo "------------------------------------------------------"
    fi
    echo "Create a folder to put all the binary files."
    echo "------------------------------------------------------"
    echo "mkdir -p target/ci/$PROJECT_BUILD_NAME"
    mkdir -p "target/ci/$PROJECT_BUILD_NAME"
    echo "ls -l target/ci/$PROJECT_BUILD_NAME"
    ls -l "target/ci/$PROJECT_BUILD_NAME"

    echo "------------------------------------------------------"
    echo "cp target/scala-2.11/*-one-jar.jar target/ci/$PROJECT_BUILD_NAME/"
    cp target/scala-2.11/*-one-jar.jar "target/ci/$PROJECT_BUILD_NAME/"
    echo "------------------------------------------------------"
    echo "ls -lR target/ci/$PROJECT_BUILD_NAME/"
    ls -lR "target/ci/$PROJECT_BUILD_NAME"
    echo "------------------------------------------------------"
    echo "Copying all binary files to 'target/ci', Done!"
    echo "======================================================"
  fi

  echo "Deploying to GitHub"
  if sbt checkGithubCredentials releaseOnGithub ; then
    echo "Deploying to GitHub: Done"
    if sbt s3-upload ; then
      echo "Uploading to S3: Done"
    else
      echo "============================================"
      echo "Build and Deploy: Failed"
      echo "============================================"
      cleanUp
      exit 1
    fi
  else
    echo "============================================"
    echo "Build and Deploy: Failed"
    echo "============================================"
    cleanUp
    exit 1
  fi
  cleanUp

  echo "============================================"
  echo "Build and Deploy: Done"
  echo "============================================"
else
  echo "============================================"
  echo "It is not release branch so skip deployment."
  echo "Build: Done"
  echo "============================================"
fi
