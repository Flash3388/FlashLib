# Contributing to FlashLib

So you want to contribute to FlashLib. Great! Because we need you! But there are several contribution rules that will not only 
make sure your changes are accepted, but will also help to maximize your contribution. Please remember to follow the rules here, 
and behave with professionalism.


## General Rules

- Code must be maintainable, even if you are no longer working on the project.
- Code should be well documented.
- Excluding bug fixes, changes in one language generally need to have changes in other languages.
  - Some changes are specific to that version of FlashLib alone.
  - If you find yourself unable to make the same changes in the other language, you can always ask for help!
 - Changes should be tested before being submited.
 - Since FlashLib is a library, code should be generally written in a general format, meaning:
   - It should be able to handle most situations related to it.
   - It needs to be dynamic enough to fit an array of user coding styles.

## What to Contribute

- Bug reports and fixes
  - Bug fixes will be usually accepted without to much questions.
  - If fixes are only implemented in one language, we will implement them for the other necessary languages. 
  - Bug reports are definitely welcome! Please submit them to the GitHub issue tracket, and remember to mark them with
    fitting labels.
- While we do welcome any changes and additions, there are several rules to consider:
  - Features must be added to both flashlibc and flashlibj, with rare exceptions.
  - Ask about large changes before spending a bunch of time on them! You can create a new issue on our GitHub tracker for 
    feature request/discussion and talk about it with us there.
  - As a rule, we are happy with the general structure of FlashLib. We are not interested in major rewrites of all of FlashLib. 
    We are open to talking about ideas, but backwards compatibility is very important for FlashLib, so be sure to keep this in 
    mind when proposing major changes.

## Submitting Changes

### Pull Request Format

Changes should be submitted as a Pull Request against the master branch of FlashLib. For most changes, we ask that you squash your 
changes down to a single commit. For particularly large changes, multiple commits are ok, but assume one commit unless asked otherwise. 
No change will be merged unless it is up to date with the current master. We will also not merge any changes with merge commits in them;
please rebase off of master before submitting a pull request. We do this to make sure that the git history isn't too cluttered.

### Merge Process

When you first submit changes, we will attempt to check it with gradle. If this fails, you will need to fix any issues that it sees. 
Once it passes, we will begin the review process in more earnest. One or more FlashLib team members will review your change. Once we 
are satisfied that your change is ready, we will test it. This will means running a full gamut of checks, including integration tests 
on actual hardware. Once all tests have passed and we are is satisfied, we will merge your change into the FlashLib repository.

## Licensing

By contributing to FlashLib, you agree that your code will be distributed with FlashLib, and licensed under the license for the FlashLib 
project. You should not contribute code that you do not have permission to relicense in this manner. 
Our license is the BSD 3-clause which can be found [here](LICENSE.md)

