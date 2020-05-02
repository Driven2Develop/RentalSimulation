# SMRental
I added travis! [![Build Status](https://travis-ci.org/NuclearBanane/SMRental.svg?branch=master)](https://travis-ci.org/NuclearBanane/SMRental)
## Contributing to our Project

* Get Intellij installed on your platform [here](https://www.jetbrains.com/)
  * You can use your uOttawa email to get a free pro license, highly recommended!
  * Don't forget to grab the JDK and JRE 8 [here](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
     * __osx__: I strongly recommend you update java via homebrew, see below
* Get Git installed and setup
   * This come default with osX and linux but your version might need updating
      * __osx__: Update using the [homebrew project](https://brew.sh/)
      * __linux__: If you are using linux, you probably know this :P
      * __windows__: Download [gitbash](https://git-scm.com/downloads)
   * Setting up your username [githubs tutorial](https://help.github.com/articles/setting-your-username-in-git/)
   * Setting up SSH [github is the real MVP](https://help.github.com/articles/connecting-to-github-with-ssh/)
* Clone this repository
   * Using Git CLI or Git bash:
      * Run ```$ git clone git@github.com:NuclearBanane/SMRental.git```
* Import project into Intellij
   * Launch Intellij and click "Import Project from Sources"
   * Before clicking continue, make sure the SDK has found JDK8.
      * If JDK8 isn't detected, then navigate to the install directory
      * __os__: Message me if you have issues
   * Keep clicking continue and ok.
* Test the configuration
   * Right click on Experiment and hit ```main.run()```
* Get the [Zenhub](https://www.zenhub.com/) browser plugin
   * I use this for project management

## Work flow
* Finding a task (Github & Zenhub)
  * Using Zenhub (Boards section on the repository or [zenhub.com](zenhub.com))
  * Find an issue in the "New issues" column
    * Click one that have no body assigned (Will have the grey and white figure as an icon)
	* In the assignees tab on the right, assign yourself
  * Close the "task window" and drag the task to the "work in progress" column
* Preparing your work environment (Git Bash)
  * Update your local branch with master
    * This will ensure that you are starting your version with all the updates with the master branch.
	  * Make sure you are on the master branch run ```$ git checkout master```
    * run ```$ git pull```
  * Before you start your work
    * create a local branch in git
      * ```$ git checkout -b mycool_branch```
* Pushing your code to git github
  * __before reading__: These are 2 terms you should understand
    * Commit: A set of changes from 1 version to a new version
	  * People will say ```"Did you commit your changes?"``` meaning, ```did you create a commit and push it to the repository.``` just an FYI
	* Push: To PUSH a set of changes (a commit) to a repository
  * Create a commit
    * run ```$ git status``` to see the files you've changed. Typically red means an uncommited change to a file. Note that it will say "Added", "Changed", or "Removed" depending on if its a new file, you've changed the file, or deleted the file.
	* Add all your files to `staging`
	  * run ```$ git add /src/your/change/file.java /src/other/file.java```
	    * Note: be careful with using ```$ git add --all```
	* Create your Commit
	  * Note: This will create a commit of changed from your current version and branch to a new version on this branch. If there are new changes on Github(origin) then you will have problems doing so if someone else is using your branch.
	  * run ```$ git commit -m "I added the output variables for RG.Vans"```
	    * Please be descriptive with you messages.
	  * If you know VIM or want to learn VIM, then removing the -m will bring up the vim editor which give your more tools to craft a message. I won't be teaching VIM to anyone
  * Pushing to github
    * This will be effectively sharing your work on github in your branch
	* If you have just created your branch or never pushed to github(origin)
	  * run ```$ git push origin mycool_branch```
	* If you have already pushed to github on this:
	  * run ```$ git push```
    * And your nearly done!
* Getting your changes reviewed and adopted
  * Create a pull request
    * go to [our reposity page](github.com/NuclearBanane/SMRental)
	* Click on "New Pull Request"
	* Select your branch as the compare branch, have base branch be master
	* tag 2 members of the group as reviewers. They will be the ones to merge the code into master
  * Managing the task
    * Go to Zenhub and drag your ticket from the "In Progress" tab to "Review/QA"
  * If you want to keep working on another ticket, make sure you got back to master and create a new branch while your fixes are getting reviewed.
  * Finishing up
    * Once your issue gets accepted, move the issue to the done column and you are done.

## Don't forget
* Try to push 1 commit at a time, you can use ```$ git commit --amend -m "my new message" ``` to change your commit if you realized you goofed
