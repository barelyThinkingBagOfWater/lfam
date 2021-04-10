The metrics gathering doesn't work with Redis clusters. The exporter needs a specific Prometheus configuration, it's messy : https://github.com/oliver006/redis_exporter.

For now I'm simply scraping the metrics of the first node of the cluster
