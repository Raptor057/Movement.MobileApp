using Essency.Stock.Movement.Maui.Views;

namespace Essency.Stock.Movement.Maui
{
    public partial class AppShell : Shell
    {
        public AppShell()
        {
            InitializeComponent();
            CurrentItem = Items.FirstOrDefault(x => x.Route == "LoginPage");
        }
    }
}
