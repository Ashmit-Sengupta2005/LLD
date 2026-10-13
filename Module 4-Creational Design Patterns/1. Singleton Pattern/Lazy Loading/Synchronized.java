// using synchronized keyword on getInstance Method

    class Synchronized{
        private static  Synchronized instance; // single instance
        private Synchronized(){}
        public static synchronized Synchronized getInstance(){
            if(instance==null){
                instance=new Synchronized();
            }
            return instance;
        }
}