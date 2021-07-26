package com.example.hushnew;
/**
 Model class to get details of a conversation
 **/
public class Conv {



        public boolean seen;
        public long timestamp;

        public Conv(){

        }

        public boolean isSeen() {
            return seen;
        }

        public void setSeen(boolean seen) {
            this.seen = seen;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public Conv(boolean seen, long timestamp) {
            this.seen = seen;
            this.timestamp = timestamp;
        }
}
