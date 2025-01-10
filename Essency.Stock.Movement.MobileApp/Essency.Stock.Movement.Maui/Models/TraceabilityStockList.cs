using SQLite;

namespace Essency.Stock.Movement.Maui.Models
{
    public class TraceabilityStockList
    {
        //[MaxLength(10)]

        [PrimaryKey, AutoIncrement]
        public int ID { get; set; }

        [NotNull]
        public long IDStock { get; set; }

        [NotNull]
        public bool Saved { get; set; }

        [NotNull]
        public bool SendByEmail { get; set; }

        [NotNull]
        public DateTime TimeStamp { get; set; }
    }
}
