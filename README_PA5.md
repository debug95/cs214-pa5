# Refer to the Codio Guide for the most up-to-date PA5 instructions

# PA5 - K-means Clustering

# Motivation
Last sprint, we implemented **user-based** collaborative filtering to predict unknown user ratings. While this filled in some gaps, it still left many ratings unknown. 

In this sprint, you will implement an **item-based** strategy to help us recommend new songs to users. Instead of relying only on similarities between users, we will also focus on **relationships between songs**.

Assume a user chooses one or more songs they like from our database. For each song they choose, we will generate a list of similar songs that they may also enjoy.

# Tasks
- Predict all missing ratings, and re-normalize.
- Perform Kmeans Clustering with the filled data.

### Preparing Song Data
In order for kmeans clustering to work properly, we need to have complete data. In this context, complete means there is a rating for every user-song pair. 
- First, fill in empty ratings using the prediction strategy from PA4.
- For any remaining unpredicted ratings, fill them in with this weighted average:
```
unpredicted = round((songMean * numSongRatings + userMean * numUserRatings) / (numSongRatings + numUserRatings))
```

- `songMean` is the average rating of the song (before predictions).
- `userMean` is the average rating of the user (before predictions).
- `numSongRatings` is the number of actual ratings the song has received.
- `numUserRatings` is the number of actual ratings the user has given.

You should now have a complete dataset of **integer** ratings that you will use for clustering. Re-normalize this data with respect to the songs, using the song means and standard deviations. Include the newly predicted values in the calculations. This is your hybrid data for clustering.

### Kmeans Clustering
[Kmeans clustering](https://en.wikipedia.org/wiki/K-means_clustering) is an algorithm that will partition songs into groups, such that intra-group dissimilarity is minimized.

To begin, the user selects `k` songs (where `k >= 1`). The normalized ratings of each selection will serve as the initial centroids for the `k` clusters. Note that the centroid is **not** a song. Although a song's ratings may be used to seed a centroid, that song may not even end up in that cluster at the end.

The algorithm is as follows:
- Initialize `k` centroids from user selections
- Repeat for `N` iterations:
  - Clear the songs from each cluster, but keep the centroid value
  - For each song in the input dataset (song order does not matter):
    - Assign the song to the cluster whose centroid is most similar (check the centroids in the order they were given by the user)
  - Recalculate the centroid of each cluster as the mean of the ratings of the songs assigned to it

In our application, `N = 10`, so you will perform this process for 10 iterations.

You are not directly comparing songs with each other, instead you are comparing each song with the centroids. Use **Euclidean Distance** to determine which centroid is most similar to the song.

**When looking for the closest centroid, loop through your centroids in the same order they were given by the user. Otherwise, your clustering results may differ.**

### Calculating Centroids
The centroid is just the average rating vector of all songs assigned to its cluster. To calculate the centroid, you sum all respective ratings of each song, and divide each by the number of songs. This results in a single list that represents the average song in the cluster. Your ratings will be normalized, this is just an example.
```
cluster:
  centroid = [?]
  songs = [[1, 2, 1],
          [2, 2, 5],
          [4, 1, 3]]

cluster.centroid = ([1, 2, 1] + [2, 2, 5] + [4, 1, 3]) / 3
= [7, 5, 9] / 3
= [2.33, 1.67, 3]
```

### Example:
```
song1,user1,2
song2,user1,3
song4,user1,4
song1,user2,2
song2,user2,3
song4,user2,5
song1,user3,4
song3,user3,5
song4,user3,4
song5,user3,5
song1,user4,1
song3,user4,2
song4,user4,1
song5,user5,3
song6,user5,2
```

#### Step 0.
Here are the actual ratings
| |user1 | user2 | user3 | user4 | user5 |
|---|---|---|---|---|---|
| **song1** | 2 | 2 | 4 | 1 | - |
| **song2** | 3 | 3 | - | - | - |
| **song3** | - | - | 5 | 2 | - |
| **song4** | 4 | 5 | 4 | 1 | - |
| **song5** | - | - | 5 | - | 3 |
| **song6** | - | - | - | - | 2 |

#### Step 1.
Predict ratings using the strategy from PA4. This may not fill in every missing rating.

| |user1 | user2 | user3 | user4 | user5 |
|---|---|---|---|---|---|
| **song1** | 2 | 2 | 4 | 1 | ***2*** |
| **song2** | 3 | 3 | ***5*** | ***1*** | ***NaN*** |
| **song3** | ***4*** | ***5*** | 5 | 2 | ***3*** |
| **song4** | 4 | 5 | 4 | 1 | ***2*** |
| **song5** | ***4*** | ***5*** | 5 | ***2*** | 3 |
| **song6** | ***NaN*** | ***NaN*** | ***4*** | ***NaN*** | 2 |

#### Step 2.
Fill in the remaining missing ratings with the weighted average calculation. Do not include the predictions made in the previous step in your calculations for the weighted average.


| |user1 | user2 | user3 | user4 | user5 |
|---|---|---|---|---|---|
| **song1** | 2 | 2 | 4 | 1 | 2 |
| **song2** | 3 | 3 | 5 | 1 | ***3*** |
| **song3** | 4 | 5 | 5 | 2 | 3 |
| **song4** | 4 | 5 | 4 | 1 | 2 |
| **song5** | 4 | 5 | 5 | 2 | 3 |
| **song6** | ***3*** | ***3*** | 4 | ***2*** | 2 |

#### Step 3.
It may be the case that some songs have one distinct rating. Remove those songs from the dataset before continuing.

#### Step 4.
Normalize the ratings with respect to the songs.
| |user1 | user2 | user3 | user4 | user5 |
|---|---|---|---|---|---|
| **song1** | -0.2041241452319317 | -0.2041241452319317 | 1.8371173070873834 | -1.2247448713915894 | -0.2041241452319317 |
| **song2** | 0.0 | 0.0 | 1.5811388300841895 | -1.5811388300841895 | 0.0 |
| **song3** | 0.171498585142509 | 1.0289915108550534 | 1.0289915108550534 | -1.5434872662825796 | -0.6859943405700353 |
| **song4** | 0.5443310539518172 | 1.224744871391589 | 0.5443310539518172 | -1.4969103983674978 | -0.8164965809277261 |
| **song5** | 0.171498585142509 | 1.0289915108550534 | 1.0289915108550534 | -1.5434872662825796 | -0.6859943405700353 |
| **song6** | 0.2672612419124246 | 0.2672612419124246 | 1.6035674514745466 | -1.0690449676496974 | -1.0690449676496974 |

#### Step 5.
Initialize `k` centroids using the ratings of the user selections, and perform kmeans clustering. Assume the user selected `song3`, `song5`, and `song6`.
```
cluster1:
  centroid: [0.171498585142509, 1.0289915108550534, 1.0289915108550534, -1.5434872662825796, -0.6859943405700353]
  songs: []

cluster2:
  centroid: [0.171498585142509, 1.0289915108550534, 1.0289915108550534, -1.5434872662825796, -0.6859943405700353]
  songs: []

cluster3:
  centroid: [0.2672612419124246, 0.2672612419124246, 1.6035674514745466, -1.0690449676496974, -1.0690449676496974]
  songs: []
```
After 10 iterations of clustering:
```
cluster1:
  centroid: [0.5443310539518172, 1.224744871391589, 0.5443310539518172, -1.4969103983674978, -0.8164965809277261]
  songs: [song4]

cluster2:
  centroid: [0.171498585142509, 1.0289915108550534, 1.0289915108550534, -1.5434872662825796, -0.6859943405700353]
  songs: [song3, song5]

cluster3:
  centroid: [0.02104569889349764, 0.02104569889349764, 1.6739411962153732, -1.2916428897084922, -0.4243897042938764]
  songs: [song1, song2, song6]
```

### Error Handling
#### General
- All previous error cases apply to their respective features.
#### Applying to `-r` Argument
- All user similarity and user prediction errors apply.
- If the input contains fewer than `k + 1` songs after predicting then removing songs with one distinct rating, treat that as an error.
- If any user selection is not found in the input data, treat that as an error.
- If the user provides no song selections after the `-r` flag, treat that as an error.
- If there are any duplicate user selections, treat that as an error.
- If all resulting clusters are empty or only include user selections, treat that as an error.

### Output
Your output should be a CSV file with columns `user choice, recommendation`, and each row contains the name of an initial selection and a song from its final cluster. These will be sorted lexicographically by the `user choice` column, and then by the `recommendation` column. Ignore any user selections in the clusters, and do not include those in the output. If a cluster is empty or only includes user selections, then no rows will be included for that user selection.

### Running Your Program
There are now **four different ways** to run your program. The first argument is the input CSV file and the second argument is the output CSV file. 

- When two arguments are passed to your program, you will write the song rating stats to a CSV file corresponding to the second arg.
- When a third argument `-a` is passed to your program, then you will write the user profile to a CSV file corresponding to the second arg.
- When a third argument `-u` is passed to your program, then you will write the user similarities to a CSV file corresponding to the second arg.
- When a third argument `-p` is passed to your program, you will write the user predictions to a CSV file corresponding to the second arg.
- When a third argument `-r` is passed to your program, you will write the song recommendations to a CSV file corresponding to the second arg.

These are independent from one another, so **only one file** is created **in either case**, only the contents of the output file changes depending on the presence of the third arg`-a`, `-u`, `-p`, or `-r`. To accept command line arguments, utilize the `String[] args` parameter in your main method. 

Do not hard code the input or output files. The user of your program must be able to specify which input CSV they want to read from and where and what name they want for the output CSV.

# How to Compile and Run

`gradle build`

`gradle run -q --args="<input-file> <output-file>"`

example run `gradle run -q --args="'database/files/file.csv' 'output/output.csv'"`

# Example Input/Output
command:
```
gradle run -q --args="'database/files/file3.csv' 'output.csv' '-r' 'song3' 'song5' 'song6'"
```

output.csv
```
user choice,recommendation
song3,song4
song6,song1
song6,song2
```

command:
```
gradle run -q --args="'database/files/large_dataset.csv' 'my_rec.csv' '-r' 'song8' 'song1’”
```
my_rec.csv
```
user choice,recommendation​
song1,song5​
song1,song6​
song1,song7​
song1,song9​
song8,song2​
song8,song3​
song8,song4

```

command:
```
gradle run -q --args="'database/files/file3.csv' 'my_rec.csv' '-r'"
```
System.err
```
Error: must select at lease one song for recommendations
```

command:
```
gradle run -q --args="'database/files/one_song.csv' 'my_rec.csv' '-r' 'song1'"
```
System.err
```
Error: no songs to recommend. Songs may have been removed. Try with a larger file or fewer selections.
```

command:
```
gradle run -q --args="'database/files/one_song.csv' 'my_rec.csv' '-r' 'song2'"
```
System.err
```
Error: selected songs must have more than one distinct rating
```

# Submitting Your Homework
Your submission must be in the main branch of your GitHub repository.

# Grading Your Homework
We will pull your code from your repository. It must contain, at a minimum two Java files. One must be named `Cs214Project.java`. We will run your program by starting it with that class name. The second file must be named `TestCs214Project.java`. This will be used to run your JUnit tests. All future assignments must contain these two files, although they may contain different code as needed by the particulars of that assignment.

# Hints
- Use **Euclidean Distance** to determine which centroid is most similar to the song. Euclidean distance was also used in previous PAs. Could you use any design patterns to reuse some code? 

# Policies

All work you submit must be your own. You may not submit code written by a peer, a former student, or anyone else. You may not copy or buy code from the web. Use of generative AI tools such as ChatGPT, Claude, GitHub CoPilot, or other LLMs is prohibited. The department academic integrity policies apply.

There is a 3 day late period for this assignment but each day it is late your grade will receive a 10% deduction.
