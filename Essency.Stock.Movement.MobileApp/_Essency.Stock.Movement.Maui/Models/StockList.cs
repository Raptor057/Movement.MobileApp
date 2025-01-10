using SQLite;

namespace Essency.Stock.Movement.Maui.Models
{
    public class StockList
    {
        [PrimaryKey, AutoIncrement]
        public int ID { get; set; }

        [NotNull]
        public long IDStock { get; set; }

        [NotNull]
        public string Company { get; set; }

        [NotNull]
        public string Source { get; set; }

        public string SoucreLoc { get; set; }

        [NotNull]
        public string Destination { get; set; }


        public string DestinationLoc { get; set; }

        [NotNull]
        public string PartNo { get; set; }

        [NotNull]
        public string Rev { get; set; }

        [NotNull]
        public string Lot { get; set; }

        [NotNull]
        public int Qty { get; set; }

        [NotNull]
        public string Date { get; set; }

        [NotNull]
        public DateTime TimeStamp { get; set; }

        [NotNull]
        public string User { get; set; }

        [NotNull]
        public string ContBolNum { get; set; }
    }
}
