package example

import org.apache.spark.sql.types._
import org.apache.spark.sql._
import java.sql.Timestamp

import org.apache.log4j.{Level, Logger}

import org.apache.spark.SparkContext._

object GDelt {
  case class GDeltData ( 
    gkgRecordId: String, 
    date: Timestamp,
    allNames: String
  )

  def datasetImplementation() {
    val schema =
      StructType(
        Array(
          StructField("GKGRECORDID", StringType, nullable=true),
          StructField("DATE", TimestampType, nullable=true),
          StructField("SourceCollectionIdentifier", LongType, nullable=true),
          StructField("SourceCommonName", StringType, nullable=true),
          StructField("DocumentIdentifier", StringType, nullable=true),
          StructField("Counts", StringType, nullable=true),
          StructField("V2Counts", StringType, nullable=true),
          StructField("Themes", StringType, nullable=true),
          StructField("V2Themes", StringType, nullable=true),
          StructField("Locations", StringType, nullable=true),
          StructField("V2Locations", StringType, nullable=true),
          StructField("Persons", StringType, nullable=true),
          StructField("V2Persons", StringType, nullable=true),
          StructField("Organizations", StringType, nullable=true),
          StructField("V2Organizations", StringType, nullable=true),
          StructField("V2Tone", StringType, nullable=true),
          StructField("Dates", StringType, nullable=true),
          StructField("GCAM", StringType, nullable=true),
          StructField("SharingImage", StringType, nullable=true),
          StructField("RelatedImages", StringType, nullable=true),
          StructField("SocialImageEmbeds", StringType, nullable=true),
          StructField("SocialVideoEmbeds", StringType, nullable=true),
          StructField("Quotations", StringType, nullable=true),
          StructField("AllNames", StringType, nullable=true),
          StructField("Amounts", StringType, nullable=true),
          StructField("TranslationInfo", StringType, nullable=true),
          StructField("Extras", StringType, nullable=true)
        )
      )

    val spark = SparkSession
      .builder
      .appName("GDelt")
      .config("spark.master", "local")
      .getOrCreate()
    val sc = spark.sparkContext // If you need SparkContext object

    import spark.implicits._

    val ds = spark.read 
                  .schema(schema) 
                  .option("timestampFormat", "MMddyyhhmmss")
                  .option("delimiter", "\t")
                  .csv("/home/andre/tudelft/supercomputing/lab1/segment/20150218230000.gkg.csv")
                  .as[GDeltData]

    // val dsFilter = ds.filter(a => a.timestamp ==
        // new Timestamp(2014 - 1900, 2, 10, 1, 1, 0, 0))
      // val reduced = ds.map((x: GDeltData) => x.date)

    val countNames = ds.filter(x => x.allNames != null)
                       .flatMap(x => x.allNames.split(";"))
                       .map(line => (line.split(",")(0),1))
                       .groupByKey(_._1)
                       .reduceGroups((a, b) => (a._1, a._2 + b._2))
                       .map(_._2)

    countNames.collect.foreach(println)
  }

  def main(args: Array[String]) {
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    
    val spark = SparkSession
      .builder
      .appName("GDelt")
      .config("spark.master", "local")
      .getOrCreate()
    val sc = spark.sparkContext // If you need SparkContext object

    import spark.implicits._

    val rawData = sc.textFile("/home/andre/tudelft/supercomputing/lab1/segment/20150218230000.gkg.csv")
    // val lines = rawData.split("\n")

    // val countNames = ds.filter(x => x.allNames != null)
    //                    .flatMap(x => x.allNames.split(";"))
    //                    .map(line => (line.split(",")(0),1))
    //                    .groupByKey(_._1)
    //                    .reduceGroups((a, b) => (a._1, a._2 + b._2))
    //                    .map(_._2))

    rawData.take(2).foreach(println)

    spark.stop
  }
}
