скачиваем artists.csv и копируем в hive-server:
```
docker cp artists.csv docker-hadoop-hive-parquet-hive-server-1:/opt/
```
запускаем hive-server:
```
docker exec -it docker-hadoop-hive-parquet-hive-server-1 /bin/bash
```
в hive-server копируем в hdfs:
```
hadoop fs -copyFromLocal artists.csv /user/hive
```
создаем и загружаем таблиу в hue:
```
CREATE TABLE artist_table(mbid STRING, artist_mb STRING, artist_lastfm STRING, country_mb STRING, country_lastfm STRING, tags_mb STRING, tags_lastfm STRING, listeners_lastfm INT, scrobbles_lastfm INT, ambiguous_artist BOOLEAN)
COMMENT 'This is an artist table'
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE;
```
```
LOAD DATA INPATH '/user/hive/artists.csv'
INTO TABLE artists_table;
```

1) Исполнителя с максимальным числом скробблов:
```
select distinct mbid from artist_table
where scrobbles_lastfm in (select max(scrobbles_lastfm) from artist_table)
```
2) Самый популярный тэг на ластфм
```
select tag, count(tag) as tag_cnt from 
artist_table lateral view explode(split(tags_lastfm, "; ")) tag_table as tag
where tag != ''
group by tag
order by tag_cnt desc limit 1
```

3) Самые популярные исполнители 10 самых популярных тегов ластфм
```
with tags_table as (
select artist_lastfm, listeners_lastfm, tag
from artist_table lateral view explode(split(tags_lastfm, '; ')) tags as tag
),
top_tags_table as (
select tag, count(tag) as cnt
from tags_table
where tag != ''
group by tag
order by cnt desc
limit 10
), 
result_table as (
select distinct artist_lastfm, listeners_lastfm
from tags_table
where tag in (
select tag from top_tags_table
)
order by listeners_lastfm desc
)
select artist_lastfm, listeners_lastfm 
from result_table
limit 10
```
4) Самые популярные теги в россии
```
select tag, count(tag) as tag_cnt from 
artist_table lateral view explode(split(tags_lastfm, "; ")) tag_table as tag
where tag != '' and country_lastfm == 'Russia'
group by tag
order by tag_cnt desc 
limit 30
```
